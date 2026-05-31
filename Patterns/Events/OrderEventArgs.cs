using System;

namespace OrderManagementApi.Patterns.Events
{
    public class OrderEventArgs : EventArgs
    {
        public int OrderId { get; set; }
        public string CustomerName { get; set; }
    }
}