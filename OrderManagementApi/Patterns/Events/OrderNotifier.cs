using System;
using System.Threading.Tasks;

namespace OrderManagementApi.Patterns.Events
{
    public class OrderNotifier
    {
        public event EventHandler<OrderEventArgs> OrderCreated;

        public async Task NotifyNewOrderAsync(int orderId, string customerName)
        {
            await Task.Delay(300);

            var args = new OrderEventArgs
            {
                OrderId = orderId,
                CustomerName = customerName
            };

            _ = Task.Run(() =>
            {
                OrderCreated?.Invoke(this, args);
            });
        }
    }
}