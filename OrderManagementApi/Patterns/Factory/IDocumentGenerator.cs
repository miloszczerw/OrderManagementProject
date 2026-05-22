namespace OrderManagementApi.Patterns.Factory
{
    public interface IDocumentGenerator
    {
        string GenerateConfirmation(int orderId);
    }
}