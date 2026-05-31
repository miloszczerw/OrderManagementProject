namespace OrderManagementApi.Patterns.Factory
{
    public class TxtDocumentGenerator : IDocumentGenerator
    {
        public string GenerateConfirmation(int orderId)
        {
            return $"Dziękujemy za zamówienie!\nPotwierdzenie zamówienia nr {orderId} (Format TXT).";
        }
    }
}