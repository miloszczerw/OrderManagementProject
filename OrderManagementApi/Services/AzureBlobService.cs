using Azure.Storage.Blobs;
using System.IO;
using System.Text;
using System.Threading.Tasks;

namespace OrderManagementApi.Services
{
    public class AzureBlobService
    {
        private readonly string _connectionString;
        private readonly string _containerName = "order-documents";

        public AzureBlobService(string connectionString)
        {
            _connectionString = connectionString;
        }

        public async Task UploadDocumentAsync(string fileName, string content)
        {
            var blobServiceClient = new BlobServiceClient(_connectionString);
            var containerClient = blobServiceClient.GetBlobContainerClient(_containerName);
            await containerClient.CreateIfNotExistsAsync();

            var blobClient = containerClient.GetBlobClient(fileName);

            using var stream = new MemoryStream(Encoding.UTF8.GetBytes(content));
            await blobClient.UploadAsync(stream, overwrite: true);
        }
        public async Task UploadDocumentAsync(string fileName, Stream fileStream)
        {
            var blobServiceClient = new BlobServiceClient(_connectionString);
            var containerClient = blobServiceClient.GetBlobContainerClient(_containerName);
            await containerClient.CreateIfNotExistsAsync();

            var blobClient = containerClient.GetBlobClient(fileName);
            await blobClient.UploadAsync(fileStream, overwrite: true);
        }
    }
}