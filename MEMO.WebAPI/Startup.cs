using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Hellang.Middleware.ProblemDetails;
using MEMO.BLL.Authentication;
using MEMO.BLL.Exceptions;
using MEMO.BLL.Interfaces;
using MEMO.BLL.Services;
using MEMO.DAL.Context;
using MEMO.DAL.Entities;
using MEMO.DAL.Seeds;
using MEMO.WebAPI.Mapper;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.HttpsPolicy;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Options;
using Microsoft.IdentityModel.Tokens;
using Newtonsoft.Json;

namespace MEMO.WebAPI
{
    public class Startup
    {
        public Startup(IConfiguration configuration)
        {
            Configuration = configuration;
        }

        public IConfiguration Configuration { get; }

        // This method gets called by the runtime. Use this method to add services to the container.
        public void ConfigureServices(IServiceCollection services)
        {
            services.AddDbContext<MEMOContext>(o => o.UseSqlServer(Configuration.GetConnectionString("LocalConnection")));

            #region Identity
            services.AddIdentity<User, IdentityRole<Guid>>(o => 
            {
                o.Password.RequireDigit = false;
                o.Password.RequireLowercase = false;
                o.Password.RequireUppercase = false;
                o.Password.RequireNonAlphanumeric = false;
                o.Password.RequiredLength = 6;
            })
            .AddEntityFrameworkStores<MEMOContext>()
            .AddDefaultTokenProviders();

            string secret = Configuration.GetValue<string>("AppSettings:Secret");

            services.AddAuthentication(options =>
            {
                options.DefaultAuthenticateScheme = JwtBearerDefaults.AuthenticationScheme;
                options.DefaultChallengeScheme = JwtBearerDefaults.AuthenticationScheme;
            })
            .AddJwtBearer(options =>
            {
                options.SaveToken = true;
                options.RequireHttpsMetadata = false;
                options.TokenValidationParameters = new TokenValidationParameters()
                {
                    ValidateLifetime = true,
                    ValidateIssuer = false,
                    ValidateAudience = false,
                    ValidateIssuerSigningKey = true,
                    IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(secret)),
                    ClockSkew = TimeSpan.Zero
                };
            });

            #endregion

            #region ProblemDetails
            services.AddProblemDetails(options =>
            {
                options.IncludeExceptionDetails = ctx => false;
                options.Map<EntityNotFoundException>(ex =>
                    new ProblemDetails
                    {
                        Title = ex.Name,
                        Detail = ex.Message,
                        Status = StatusCodes.Status404NotFound
                    });
                options.Map<LoginFailedException>(ex =>
                    new ProblemDetails
                    {
                        Title = ex.Name,
                        Detail = ex.Message,
                        Status = StatusCodes.Status404NotFound
                    });
                options.Map<RegistrationFailedException>(ex =>
                    new ProblemDetails
                    {
                        Title = ex.Name,
                        Detail = ex.Message,
                        Status = StatusCodes.Status404NotFound
                    });

            });
            #endregion

            services.AddTransient<IAuthenticationService, AuthenticationService>();
            services.AddTransient<IUserService, UserService>();

            services.AddSingleton(AutoMapperConfig.Configure());
            services.AddSingleton(new TokenManager(secret));

            services.AddMvcCore(o => o.EnableEndpointRouting = false);

            services.AddMvc().SetCompatibilityVersion(CompatibilityVersion.Version_3_0)
                             .AddJsonOptions(json => json.JsonSerializerOptions.MaxDepth = 0);
        }

        // This method gets called by the runtime. Use this method to configure the HTTP request pipeline.
        public void Configure(IApplicationBuilder app, IHostingEnvironment env)
        {
            if (env.IsDevelopment())
            {
                app.UseDeveloperExceptionPage();
            }
            else
            {
                // The default HSTS value is 30 days. You may want to change this for production scenarios, see https://aka.ms/aspnetcore-hsts.
                app.UseHsts();
            }

            DataSeed.Initialize(app.ApplicationServices
                                   .GetRequiredService<IServiceScopeFactory>()
                                   .CreateScope()
                                   .ServiceProvider);

            app.UseProblemDetails();

            app.UseHttpsRedirection();
            app.UseAuthentication();
            app.UseMvc();
        }
    }
}
