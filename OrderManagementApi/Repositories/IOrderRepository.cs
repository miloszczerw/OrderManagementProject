using OrderManagementApi.Models;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace OrderManagementApi.Repositories
{
    public interface IOrderRepository
    {
        Task<IEnumerable<Order>> GetAllOrdersAsync();
        Task<Order> GetByIdAsync(int id);
        Task AddAsync(Order order);
        void Update(Order order);
        void Delete(Order order);
        Task<bool> SaveChangesAsync();
    }
}