using MEMO.DAL.Context;
using Microsoft.AspNetCore;
using Microsoft.AspNetCore.Hosting;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.DependencyInjection;

namespace MEMO.WebAPI
{
    public class Program
    {
        public static void Main(string[] args)
        {
            args = args.URLConfiguration();

            var webHost = CreateWebHostBuilder(args).Build();
            using (var scope = webHost.Services.CreateScope())
            {
                scope.ServiceProvider.GetRequiredService<MEMOContext>().Database.Migrate();
            }
            webHost.Run();
        }

        public static IWebHostBuilder CreateWebHostBuilder(string[] args) =>
            WebHost.CreateDefaultBuilder(args)
                   .UseStartup<Startup>();
    }
}
