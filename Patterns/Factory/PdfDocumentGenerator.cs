namespace OrderManagementApi.Patterns.Factory
{
    public class PdfDocumentGenerator : IDocumentGenerator
    {
        public string GenerateConfirmation(int orderId)
        {
            return $"%PDF-1.4\n1 0 obj\n<< /Type /Catalog ... >>\nPotwierdzenie dla zamówienia nr {orderId} (Format PDF)";
        }
    }
}