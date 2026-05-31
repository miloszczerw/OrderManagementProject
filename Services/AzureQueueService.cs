using Azure.Storage.Queues;
using System.Threading.Tasks;

namespace OrderManagementApi.Services
{
    public class AzureQueueService
    {
        private readonly string _connectionString;
        private readonly string _queueName = "new-orders-queue";

        public AzureQueueService(string connectionString)
        {
            _connectionString = connectionString;
        }

        public async Task SendMessageAsync(string message)
        {
            var queueClient = new QueueClient(_connectionString, _queueName);
            await queueClient.CreateIfNotExistsAsync();

            await queueClient.SendMessageAsync(message);
        }
    }
}