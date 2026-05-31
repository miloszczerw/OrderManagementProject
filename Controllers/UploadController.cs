using Microsoft.AspNetCore.Mvc;
using OrderManagementApi.Services;

namespace OrderManagementApi.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class UploadController : ControllerBase
    {
        private readonly AzureBlobService _blobService;

        public UploadController()
        {
            string storageConnString = "DefaultEndpointsProtocol=https;AccountName=magazynstudia;AccountKey=vm+EoqMULbCRhXcXxaroZlFrSjtAVwxpVR33J75euBRMQbH1l4KGviPIL/MpvPs1BuyZ7gdvHIbM+AStTiXPeQ==;EndpointSuffix=core.windows.net";
            _blobService = new AzureBlobService(storageConnString);
        }

        [HttpPost]
        public async Task<IActionResult> UploadFile(IFormFile file)
        {
            if (file == null || file.Length == 0)
                return BadRequest("Brak pliku");

            using var stream = file.OpenReadStream();
            await _blobService.UploadDocumentAsync(file.FileName, stream);

            return Ok(new { Message = "Plik zapisany w Azure", FileName = file.FileName });
        }
    }
}