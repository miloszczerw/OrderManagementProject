using OrderManagementApi.Patterns.Factory;
using System;
using Xunit;

namespace OrderManagementApi.Tests
{
    public class DocumentFactoryTests
    {
        [Fact]
        public void CreateGenerator_FormatIsPdf()
        {
            var factory = new DocumentGeneratorFactory();

            var generator = factory.CreateGenerator("pdf");

            Assert.IsType<PdfDocumentGenerator>(generator);
        }

        [Fact]
        public void CreateGenerator_FormatUnknown()
        {
            var factory = new DocumentGeneratorFactory();

            Assert.Throws<ArgumentException>(() => factory.CreateGenerator("nieznany format"));
        }
    }
}