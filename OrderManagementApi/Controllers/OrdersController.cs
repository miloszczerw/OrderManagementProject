using Microsoft.AspNetCore.Mvc;
using OrderManagementApi.Models;
using OrderManagementApi.Patterns.Events;
using OrderManagementApi.Patterns.Factory;
using OrderManagementApi.Services;
using System.Threading.Tasks;
using Microsoft.EntityFrameworkCore;

namespace OrderManagementApi.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class OrdersController : ControllerBase
    {
        private readonly AppDbContext _context;
        private readonly AzureBlobService _blobService;
        private readonly AzureQueueService _queueService;

        private readonly OrderNotifier _orderNotifier;
        private readonly DocumentGeneratorFactory _factory;

        public OrdersController(AppDbContext context)
        {
            _context = context;

            string storageConnString = "DefaultEndpointsProtocol=https;AccountName=magazynstudia;AccountKey=vm+EoqMULbCRhXcXxaroZlFrSjtAVwxpVR33J75euBRMQbH1l4KGviPIL/MpvPs1BuyZ7gdvHIbM+AStTiXPeQ==;EndpointSuffix=core.windows.net";

            _blobService = new AzureBlobService(storageConnString);
            _queueService = new AzureQueueService(storageConnString);

            _orderNotifier = new OrderNotifier();
            _factory = new DocumentGeneratorFactory();

            _orderNotifier.OrderCreated += (sender, args) =>
            {
                System.Console.WriteLine($"[Zdarzenie] Nowe zamówienie ID: {args.OrderId} od {args.CustomerName}");
            };
        }

        [HttpPost]
        public async Task<IActionResult> CreateOrder([FromBody] Order order)
        {
            _context.Orders.Add(order);
            await _context.SaveChangesAsync();

            await _orderNotifier.NotifyNewOrderAsync(order.Id, order.CustomerName);

            var generator = _factory.CreateGenerator(order.DocumentFormat);
            string documentContent = generator.GenerateConfirmation(order.Id);

            string fileName = $"Order_{order.Id}.{order.DocumentFormat.ToLower()}";
            await _blobService.UploadDocumentAsync(fileName, documentContent);

            await _queueService.SendMessageAsync($"Nowe zamowienie. ID: {order.Id}");

            return Ok(new { Message = "Zamówienie złożone pomyślnie i przetworzone w chmurze Azure", OrderId = order.Id });
        }
        [HttpGet]
        public async Task<IActionResult> GetOrders()
        {
            var orders = await _context.Orders.ToListAsync();
            return Ok(orders);
        }

        [HttpPut("{id}")]
        public async Task<IActionResult> UpdateOrder(int id, [FromBody] Order updatedOrder)
        {
            var order = await _context.Orders.FindAsync(id);
            if (order == null) return NotFound();

            order.CustomerName = updatedOrder.CustomerName;
            order.ProductId = updatedOrder.ProductId;
            order.Quantity = updatedOrder.Quantity;
            order.DocumentFormat = updatedOrder.DocumentFormat;

            await _context.SaveChangesAsync();

            return Ok(order);
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteOrder(int id)
        {
            var order = await _context.Orders.FindAsync(id);
            if (order == null) return NotFound();

            _context.Orders.Remove(order);
            await _context.SaveChangesAsync();
            return Ok();
        }
    }
}