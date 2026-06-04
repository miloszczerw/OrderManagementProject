using Azure.Storage.Blobs;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Configuration;
using OrderManagementApi.Models;
using OrderManagementApi.Patterns.Events;
using OrderManagementApi.Patterns.Factory;
using OrderManagementApi.Repositories;
using OrderManagementApi.Services;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Security.Claims;
using System.Threading.Tasks;

namespace OrderManagementApi.Controllers
{
    [Authorize]
    [Route("api/[controller]")]
    [ApiController]
    public class OrdersController : ControllerBase
    {
        private readonly IOrderRepository _repository;
        private readonly AzureBlobService _blobService;
        private readonly AzureQueueService _queueService;
        private readonly OrderNotifier _orderNotifier;
        private readonly IConfiguration _configuration;

        public OrdersController(
            IOrderRepository repository,
            AzureBlobService blobService,
            AzureQueueService queueService,
            OrderNotifier orderNotifier,
            IConfiguration configuration)
        {
            _repository = repository;
            _blobService = blobService;
            _queueService = queueService;
            _orderNotifier = orderNotifier;
            _configuration = configuration;
        }

        [HttpPost("create-with-image")]
        public async Task<IActionResult> CreateOrder(
            [FromForm] string productName,
            [FromForm] int quantity,
            [FromForm] double price,
            [FromForm] IFormFile? imageFile)
        {
            var userIdClaim = User.FindFirst(ClaimTypes.NameIdentifier)?.Value
                              ?? User.FindFirst("id")?.Value
                              ?? User.FindFirst(ClaimTypes.Name)?.Value;

            if (string.IsNullOrEmpty(userIdClaim) || !int.TryParse(userIdClaim, out int loggedInUserId))
            {
                var claims = string.Join(", ", User.Claims.Select(c => $"{c.Type}={c.Value}"));
                return Unauthorized(new { Message = $"Nieprawidłowy token. Znalezione claims: {claims}" });
            }

            string imageUrl = string.Empty;

            if (imageFile != null && imageFile.Length > 0)
            {
                try
                {
                    string connectionString = _configuration.GetConnectionString("AzureStorageConnection");
                    BlobServiceClient blobServiceClient = new BlobServiceClient(connectionString);
                    BlobContainerClient containerClient = blobServiceClient.GetBlobContainerClient("product-images");

                    string fileName = $"{Guid.NewGuid()}_{Path.GetFileName(imageFile.FileName)}";
                    BlobClient blobClient = containerClient.GetBlobClient(fileName);

                    using (var stream = imageFile.OpenReadStream())
                    {
                        await blobClient.UploadAsync(stream, true);
                    }

                    imageUrl = blobClient.Uri.ToString();
                }
                catch (Exception ex)
                {
                    return StatusCode(500, new { Message = $"Błąd Azure Blob Storage: {ex.Message}" });
                }
            }

            var order = new Order
            {
                ProductName = productName,
                Quantity = quantity,
                Price = (decimal)price,
                UserId = loggedInUserId,
                OrderDate = DateTime.UtcNow,
                Status = "Pending",
                ImageUrl = imageUrl,
                TotalAmount = (decimal)(price * quantity)
            };

            await _repository.AddAsync(order);
            await _repository.SaveChangesAsync();

            await _orderNotifier.NotifyNewOrderAsync(order.Id, order.ProductName);
            await _queueService.SendMessageAsync($"Nowe zamowienie. ID: {order.Id}");

            return Ok(new { Message = "Zamówienie złożone pomyślnie", OrderId = order.Id, ImageUrl = imageUrl });
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<Order>>> GetOrders()
        {
            var orders = await _repository.GetAllOrdersAsync();
            return Ok(orders);
        }

        [HttpPut("{id}")]
        public async Task<IActionResult> UpdateOrder(int id, [FromBody] Order updatedOrder)
        {
            var order = await _repository.GetByIdAsync(id);
            if (order == null) return NotFound();

            order.ProductName = updatedOrder.ProductName;
            order.Quantity = updatedOrder.Quantity;
            order.Price = updatedOrder.Price;
            order.TotalAmount = (decimal)(updatedOrder.Price * updatedOrder.Quantity);

            _repository.Update(order);
            await _repository.SaveChangesAsync();

            return Ok(order);
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteOrder(int id)
        {
            var order = await _repository.GetByIdAsync(id);
            if (order == null) return NotFound();

            _repository.Delete(order);
            await _repository.SaveChangesAsync();
            return Ok();
        }

        [HttpPatch("{id}/status")]
        public async Task<IActionResult> UpdateStatus(int id, [FromBody] string newStatus)
        {
            var order = await _repository.GetByIdAsync(id);
            if (order == null) return NotFound(new { Message = $"Nie znaleziono zamówienia o ID {id}" });

            order.Status = newStatus;

            _repository.Update(order);
            await _repository.SaveChangesAsync();

            await _queueService.SendMessageAsync($"Zmiana statusu zamowienia ID: {order.Id} na {newStatus}");

            return Ok(order);
        }

        [Authorize]
        [HttpGet("stats")]
        public async Task<IActionResult> GetStats()
        {
            var orders = await _repository.GetAllOrdersAsync();

            var totalSales = orders.Sum(o => (decimal)o.TotalAmount);

            var pendingCount = orders.Count(o => o.Status.Replace("\"", "").ToLower().Trim() == "pending");
            var shippedCount = orders.Count(o => o.Status.Replace("\"", "").ToLower().Trim() == "shipped");
            var completedCount = orders.Count(o => o.Status.Replace("\"", "").ToLower().Trim() == "completed");

            return Ok(new
            {
                TotalSales = totalSales,
                PendingCount = pendingCount,
                ShippedCount = shippedCount,
                CompletedCount = completedCount
            });
        }
    }
}