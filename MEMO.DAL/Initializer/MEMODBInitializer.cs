using MEMO.DAL.Context;
using MEMO.DAL.Entities;
using MEMO.DTO.Enums;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.DependencyInjection;
using System;
using System.Linq;

namespace MEMO.DAL.Initializer
{
    public class MEMODBInitializer
    {
        public static async void Initialize(IServiceProvider serviceProvider)
        {
            var context = serviceProvider.GetRequiredService<MEMOContext>();
            var userManager = serviceProvider.GetRequiredService<UserManager<User>>();
            var roleManager = serviceProvider.GetRequiredService<RoleManager<IdentityRole<Guid>>>();

            context.Database.Migrate();

            if (!await roleManager.RoleExistsAsync("Administrator"))
            {
                var adminRole = new IdentityRole<Guid>("Administrator");
                await roleManager.CreateAsync(adminRole);
            }

            if (!await roleManager.RoleExistsAsync("User"))
            {
                var userRole = new IdentityRole<Guid>("User");
                await roleManager.CreateAsync(userRole);
            }

            Language language1;
            Language language2;

            if (!context.Languages.Any())
            {
                language1 = new Language { Code = "hu", Name = "Magyar" };
                context.Add(language1);

                language2 = new Language { Code = "en", Name = "Angol" };
                context.Add(language2);
            }
            else
            {
                language1 = context.Languages.Where(l => l.Code == "hu").SingleOrDefault();
                language2 = context.Languages.Where(l => l.Code == "en").SingleOrDefault();
            }

            User admin;
            User user;

            if (!context.Users.Any())
            {
                admin = new User()
                {
                    Email = "admin@admin.xpl",
                    SecurityStamp = Guid.NewGuid().ToString(),
                    UserName = "Admin"
                };

                await userManager.CreateAsync(admin, "qwertz");

                await userManager.AddToRoleAsync(admin, "Administrator");

                user = new User()
                {
                    Email = "user@user.xpl",
                    SecurityStamp = Guid.NewGuid().ToString(),
                    UserName = "User"
                };

                await userManager.CreateAsync(user, "123456");

                await userManager.AddToRoleAsync(user, "User");
            }
            else
            {
                admin = await userManager.FindByNameAsync("Admin");
                user = await userManager.FindByNameAsync("User");
            }

            Dictionary dictionary;

            if (!context.Dictionaries.Any())
            {
                dictionary = new Dictionary { Name = "Seed", IsPublic = true };
                context.Add(dictionary);
            }
            else
            {
                dictionary = context.Dictionaries.SingleOrDefault(d => d.Name == "Seed");
            }

            Translation translation;

            if (!context.Translations.Any())
            {
                translation = new Translation { Value = "Test", Dictionary = dictionary };
                context.Add(translation);
            }
            else
            {
                translation = context.Translations.FirstOrDefault(t => t.Value == "Test");
            }

            MetaDefinition metaDefinition;

            if (!context.MetaDefinitions.Any())
            {
                metaDefinition = new MetaDefinition { Name = "Test", Dictionary = dictionary, Type = MetaType.Spinner };
                context.Add(metaDefinition);
            }
            else
            {
                metaDefinition = context.MetaDefinitions.FirstOrDefault(md => md.Name == "Test");
            }

            MetaDefinitionParameter metaDefinitionParameter;

            if (!context.MetaDefinitionParameters.Any())
            {
                metaDefinitionParameter = new MetaDefinitionParameter { Value = "Test", MetaDefinition = metaDefinition };
                context.Add(metaDefinitionParameter);
            }
            else
            {
                metaDefinition = context.MetaDefinitions.FirstOrDefault(md => md.Name == "Test");
            }

            TranslationMeta translationMeta;

            if (!context.TranslationMetas.Any())
            {
                translationMeta = new TranslationMeta { Value = "Sentence", Translation = translation, MetaDefinition = metaDefinition };
                context.Add(translationMeta);
            }
            else
            {
                translationMeta = context.TranslationMetas.FirstOrDefault(tm => tm.Value == "Sentence");
            }

            if (!context.DictionaryLanguages.Any())
            {
                context.Add(new DictionaryLanguage { Type = LanguageType.source, Dictionary = dictionary, Language = language1 });
                context.Add(new DictionaryLanguage { Type = LanguageType.destination, Dictionary = dictionary, Language = language2 });
            }

            if (!context.UserDictionaries.Any())
            {
                context.Add(new UserDictionary { Type = UserType.owner, Dictionary = dictionary, User = admin });
                context.Add(new UserDictionary { Type = UserType.viewer , Dictionary = dictionary, User = user });
            }

            await context.SaveChangesAsync();
        }
    }
}
