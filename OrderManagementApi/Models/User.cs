namespace OrderManagementApi.Models
{
    public class User
    {
        public int Id { get; set; }
        public string Email { get; set; } = string.Empty;
        public string PasswordHash { get; set; } = string.Empty;
        public string CompanyName { get; set; } = string.Empty; 
        public List<Order> Orders { get; set; } = new();
        public string Role { get; set; } = "Customer";
    }

}