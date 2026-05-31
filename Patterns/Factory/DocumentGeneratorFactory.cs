using System;

namespace OrderManagementApi.Patterns.Factory
{
    public class DocumentGeneratorFactory
    {
        public IDocumentGenerator CreateGenerator(string format)
        {
            if (string.IsNullOrWhiteSpace(format))
                throw new ArgumentException("Format nie może być pusty.");

            return format.ToLower() switch
            {
                "txt" => new TxtDocumentGenerator(),
                "pdf" => new PdfDocumentGenerator(),
                _ => throw new ArgumentException($"Nieobsługiwany format dokumentu: {format}")
            };
        }
    }
}