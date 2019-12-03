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
using MEMO.DAL.DataSeed;
using MEMO.BLL.Mapper;
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
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Options;
using Microsoft.IdentityModel.Tokens;
using Newtonsoft.Json;
using MEMO.BLL.Authorization;

namespace MEMO.WebAPI
{
    public class Startup
    {
        public Startup(IConfiguration configuration)
        {
            Configuration = configuration;
        }

        public IConfiguration Configuration { get; }

        public void ConfigureServices(IServiceCollection services)
        {
            #region DbContext

            services.AddDbContext<MEMOContext>(o => 
                o.UseSqlServer(Configuration.GetConnectionString("LocalConnection")));
            
            #endregion

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
                    IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(secret))
                    #if(DEBUG)
                    ,ClockSkew = TimeSpan.Zero
                    #endif
                };
            });

            #endregion

            #region ProblemDetails

            services.AddProblemDetails(options =>
            {
                options.IncludeExceptionDetails = ctx => false;
                options.Map<TranslationException>(ex =>
                    new ProblemDetails
                    {
                        Title = ex.Name,
                        Detail = ex.Message,
                        Status = StatusCodes.Status404NotFound
                    });
                options.Map<EntityInsertException>(ex =>
                    new ProblemDetails
                    {
                        Title = ex.Name,
                        Detail = ex.Message,
                        Status = StatusCodes.Status404NotFound
                    });
                options.Map<EntityUpdateException>(ex =>
                    new ProblemDetails
                    {
                        Title = ex.Name,
                        Detail = ex.Message,
                        Status = StatusCodes.Status404NotFound
                    });
                options.Map<EntityDeleteException>(ex =>
                    new ProblemDetails
                    {
                        Title = ex.Name,
                        Detail = ex.Message,
                        Status = StatusCodes.Status404NotFound
                    });
                options.Map<AuthorizationException>(ex =>
                    new ProblemDetails
                    {
                        Title = ex.Name,
                        Detail = ex.Message,
                        Status = StatusCodes.Status404NotFound
                    });
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

            #region ModelServices

            services.AddTransient<IAuthenticationService, AuthenticationService>();
            services.AddTransient<IUserService, UserService>();
            services.AddTransient<IDictionaryService, DictionaryService>();
            services.AddTransient<ITranslationService, TranslationService>();
            services.AddTransient<ILanguageService, LanguageService>();
            services.AddTransient<IAttributeService, AttributeService>();

            #endregion

            #region Singletons

            services.AddSingleton(AutoMapperConfig.Configure());

            var tokenManager = new TokenManager(secret);
            services.AddSingleton(tokenManager);
            services.AddSingleton(new AuthorizationManager(tokenManager));

            #endregion

            #region MVC

            services.AddControllers();

            services.AddMvc().SetCompatibilityVersion(CompatibilityVersion.Version_3_0)
                             .AddNewtonsoftJson(opt => opt.SerializerSettings.ReferenceLoopHandling = ReferenceLoopHandling.Ignore);

            #endregion
        }

        public void Configure(IApplicationBuilder app, IWebHostEnvironment env, IServiceProvider serviceProvider)
        {
            if (env.IsDevelopment())
            {
                app.UseDeveloperExceptionPage();

                DevelopmentDataSeed.Initialize(app.ApplicationServices
                   .GetRequiredService<IServiceScopeFactory>()
                   .CreateScope()
                   .ServiceProvider);
            }

            app.UseProblemDetails();

            app.UseHttpsRedirection();
            app.UseRouting();
            app.UseAuthentication();
            app.UseAuthorization();

            app.UseEndpoints(endpoints =>
            {
                endpoints.MapControllers();
            });
        }
    }
}
