using OrderManagementApi.Patterns.Events;
using System.Threading.Tasks;
using Xunit;

namespace OrderManagementApi.Tests
{
    public class OrderNotifierTests
    {
        [Fact]
        public async Task NotifyNewOrderAsync()
        {
            var notifier = new OrderNotifier();
            bool eventRaised = false; 
            int receivedOrderId = 0;

            notifier.OrderCreated += (sender, args) =>
            {
                eventRaised = true;
                receivedOrderId = args.OrderId;
            };

            await notifier.NotifyNewOrderAsync(99, "Jan Kowalski");

            await Task.Delay(100);

            Assert.True(eventRaised, "Zdarzenie wywołane.");
            Assert.Equal(99, receivedOrderId);
        }
    }
}