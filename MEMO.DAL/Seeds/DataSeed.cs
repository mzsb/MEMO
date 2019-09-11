using MEMO.DAL.Context;
using MEMO.DAL.Entities;
using Microsoft.AspNetCore.Identity;
using Microsoft.Extensions.DependencyInjection;
using System;
using System.Linq;

namespace MEMO.DAL.Seeds
{
    public class DataSeed
    {
        public static async void Initialize(IServiceProvider serviceProvider)
        {
            var context = serviceProvider.GetRequiredService<MEMOContext>();
            var userManager = serviceProvider.GetRequiredService<UserManager<User>>();
            var roleManager = serviceProvider.GetRequiredService<RoleManager<IdentityRole<Guid>>>();

            context.Database.EnsureCreated();

            if (!context.Users.Any())
            {
                User user = new User()
                {
                    Email = "admin@admin.xpl",
                    SecurityStamp = Guid.NewGuid().ToString(),
                    UserName = "Admin"
                };

                await userManager.CreateAsync(user, "Password@123");

                if (!await roleManager.RoleExistsAsync("Administrator"))
                {
                    var adminRole = new IdentityRole<Guid>("Administrator");
                    await roleManager.CreateAsync(adminRole);
                }

                await userManager.AddToRoleAsync(user, "Administrator");
            }
        }
    }
}
