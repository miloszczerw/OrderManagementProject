using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using OrderManagementApi.Models;
using OrderManagementApi.Patterns.Events;
using OrderManagementApi.Patterns.Factory;
using OrderManagementApi.Repositories;
using OrderManagementApi.Services;
using System.Text;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers()
    .AddJsonOptions(options =>
    {
        options.JsonSerializerOptions.PropertyNameCaseInsensitive = true;
    });

builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

builder.Services.AddSwaggerGen(opt =>
{
    opt.AddSecurityDefinition("Bearer", new Microsoft.OpenApi.Models.OpenApiSecurityScheme
    {
        In = Microsoft.OpenApi.Models.ParameterLocation.Header,
        Description = "Wpisz token",
        Name = "Authorization",
        Type = Microsoft.OpenApi.Models.SecuritySchemeType.Http,
        BearerFormat = "JWT",
        Scheme = "bearer"
    });

    opt.AddSecurityRequirement(new Microsoft.OpenApi.Models.OpenApiSecurityRequirement
    {
        {
            new Microsoft.OpenApi.Models.OpenApiSecurityScheme
            {
                Reference = new Microsoft.OpenApi.Models.OpenApiReference
                {
                    Type=Microsoft.OpenApi.Models.ReferenceType.SecurityScheme,
                    Id="Bearer"
                }
            },
            new string[]{}
        }
    });
});

string connectionString = "Server=tcp:ordermanagementservernew.database.windows.net,1433;Initial Catalog=OrderManagementDb;User ID=miloszczerw;Password=mILOSZ123!;MultipleActiveResultSets=False;Encrypt=True;TrustServerCertificate=True;Connection Timeout=30;";
var storageConnectionString = builder.Configuration["ConnectionStrings:AzureStorageConnection"]
    ?? "DefaultEndpointsProtocol=https;AccountName=magazynstudia;AccountKey=vm+EoqMULbCRhXcXxaroZlFrSjtAVwxpVR33J75euBRMQbH1l4KGviPIL/MpvPs1BuyZ7gdvHIbM+AStTiXPeQ==;EndpointSuffix=core.windows.net";


builder.Services.AddDbContext<AppDbContext>(options =>
    options.UseSqlServer(connectionString, sqlServerOptionsAction: sqlOptions =>
    {
        sqlOptions.EnableRetryOnFailure();
    }));

builder.Services.AddSingleton(new AzureBlobService(storageConnectionString));
builder.Services.AddSingleton(new AzureQueueService(storageConnectionString));
builder.Services.AddSingleton<DocumentGeneratorFactory>();
builder.Services.AddSingleton<OrderNotifier>();
builder.Services.AddScoped<IOrderRepository, OrderRepository>();

var jwtTokenKey = builder.Configuration.GetSection("AppSettings:Token").Value
    ?? "AamIjdDAEsyre7vEtDEPAfvMtVTJHgfToiQWlhHPwMaLIyTTyYDvwx7gId5jvaPM";

builder.Services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
    .AddJwtBearer(options => {
        options.TokenValidationParameters = new TokenValidationParameters
        {
            ValidateIssuerSigningKey = true,
            IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(jwtTokenKey)),
            ValidateIssuer = false,
            ValidateAudience = false,
            ValidateLifetime = false,
            ClockSkew = TimeSpan.Zero
        };
    });

var app = builder.Build();

app.UseSwagger();
app.UseSwaggerUI();
app.UseDeveloperExceptionPage();
app.UseHttpsRedirection();

app.UseCors("AllowAll");

app.UseAuthentication();
app.UseAuthorization();

app.MapControllers();

app.Run();