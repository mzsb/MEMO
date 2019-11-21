using MEMO.DAL.Context;
using MEMO.DAL.Entities;
using MEMO.DAL.Enums;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.DependencyInjection;
using System.Collections.Generic;
using System.Linq;
using MEMO.DAL.Helpers;
using System;

namespace MEMO.DAL.DataSeed
{
    public class DevelopmentDataSeed
    {
        public static async void Initialize(IServiceProvider serviceProvider)
        {
            Random random = new Random();
            const string loremIpsum = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam justo turpis, consequat a orci id, consequat fringilla libero. Curabitur sodales, neque sit amet dapibus dignissim, magna dolor iaculis tellus, et porta quam eros ac sem.";
            string[] stringRemoves = new string[] { " ", ".", "," };
            const int userCount = 5;
            const int dictionaryCount = 6;
            const int dictionaryCountRandomRange = 5;
            const int translationCount = 10;
            const int translationCountRandomRange = 5;
            const int attributeCount = 5; 
            const int attributeCountRandomRange = 2;
            const int attributeValueCount = 3;
            const int attributeValueCountRandomRange = 2;
            const int attributeParameterCount = 3;
            const int attributeParameterCountRandomRange = 1;
            const int viewerCount = 3;

            var users = new List<User>();

            using (var userManager = serviceProvider.GetRequiredService<UserManager<User>>())
            {
                if (!userManager.Users.Any())
                {
                    for (int i = 0; i < userCount; i++) {

                        User user;
                        if (random.Next(3) == 2)
                        {
                            user = new User()
                            {
                                Email = $"admin{i}@admin.xpl",
                                SecurityStamp = Guid.NewGuid().ToString(),
                                UserName = $"Admin{i}"
                            };

                            await userManager.CreateAsync(user, "123456");

                            await userManager.AddToRoleAsync(user, "Administrator");
                        }
                        else
                        {
                            user = new User()
                            {
                                Email = $"user{i}@user.xpl",
                                SecurityStamp = Guid.NewGuid().ToString(),
                                UserName = $"User{i}"
                            };

                            await userManager.CreateAsync(user, "123456");

                            await userManager.AddToRoleAsync(user, "User");
                        }
                        users.Add(user);
                    }
                }
                else
                {
                    users = await userManager.Users.Include(u => u.Attributes).ToListAsync();
                }
            }

            using (var context = serviceProvider.GetRequiredService<MEMOContext>())
            {
                foreach (var user in users)
                {
                    var languages = await context.Languages.ToListAsync();

                    var attributes = new List<Entities.Attribute>();

                    if (!user.Attributes.Any())
                    {
                        for (int i = 0; i < attributeCount + (random.Next(2) == 0 ? random.Next(attributeCountRandomRange) : -random.Next(attributeCountRandomRange)); i++)
                        {
                            var idx = random.Next(loremIpsum.Length - 6);
                            AttributeType type;
                            switch (random.Next(3))
                            {
                                case 0: 
                                    type = AttributeType.text; break;
                                case 1:
                                    type = AttributeType.spinner; break;
                                case 2:
                                    type = AttributeType.checkbox; break;
                                default:
                                    type = AttributeType.text; break;
                            }

                            var attribute = new Entities.Attribute
                            {
                                Name = loremIpsum.CustomSubstring(idx, 6, stringRemoves),
                                User = user,
                                Type = type
                            };
                            attributes.Add(attribute);
                            context.Add(attribute);
                        }
                    }
                    else
                    {
                        attributes = await context.Attributes.Where(a => a.UserId == user.Id)
                                                                          .Include(a => a.AttributeParameters)
                                                                          
                                                                          .ToListAsync();
                    }

                    foreach (var attribute in attributes)
                    {
                        if (attribute.Type == AttributeType.spinner)
                        {
                            if (!attribute.AttributeParameters.Any())
                            {
                                for (int i = 0; i < attributeParameterCount + (random.Next(2) == 0 ? random.Next(attributeParameterCountRandomRange) : -random.Next(attributeParameterCountRandomRange)); i++)
                                {
                                    var idx = random.Next(loremIpsum.Length - 6);
                                    var attributeParameter = new AttributeParameter
                                    {
                                        Value = loremIpsum.CustomSubstring(idx, 6, stringRemoves),
                                        Attribute = attribute
                                    };
                                    context.Add(attributeParameter);
                                }
                            }
                        }
                    }

                    var dictionaries = new List<Dictionary>();

                    if (!context.UserDictionaries.Where(ud => ud.UserId == user.Id && 
                                                        ud.Type == UserType.owner)
                                                 .Any())
                    {
                        for (int i = 0; i < dictionaryCount + (random.Next(2) == 0 ? random.Next(dictionaryCountRandomRange) : -random.Next(dictionaryCountRandomRange)); i++)
                        {
                            var dictionary = new Dictionary
                            {
                                Name = $"{i}_{user.UserName}_Dictionary",
                                Description = loremIpsum.Substring(random.Next(loremIpsum.Length / 3)),
                                IsPublic = random.Next(2) == 0,
                                IsFastAccessible = random.Next(3) != 2
                            };
                            dictionaries.Add(dictionary);
                            context.Add(dictionary);
                        }
                    }
                    else
                    {
                        dictionaries = await context.UserDictionaries.Include(ud => ud.Dictionary)
                                                                     .ThenInclude(d => d.Translations)
                                                                     .ThenInclude(t => t.AttributeValues)
                                                                     .Where(ud => ud.UserId == user.Id &&
                                                                            ud.Type == UserType.owner)
                                                                     .Select(ud => ud.Dictionary)
                                                                     
                                                                     .ToListAsync();
                    }

                    foreach (var dictionary in dictionaries)
                    {
                        var translations = new List<Translation>();

                        if (!dictionary.Translations.Any())
                        {
                            for (int i = 0; i < translationCount + (random.Next(2) == 0 ? random.Next(translationCountRandomRange) : -random.Next(translationCountRandomRange)); i++)
                            {
                                var idx1 = random.Next(loremIpsum.Length - 6);
                                var idx2 = random.Next(loremIpsum.Length - 6);
                                var translation = new Translation
                                {
                                    Original = loremIpsum.CustomSubstring(idx1, 6, stringRemoves),
                                    Translated = loremIpsum.CustomSubstring(idx2, 6, stringRemoves),
                                    Dictionary = dictionary
                                };
                                translations.Add(translation);
                                context.Add(translation);
                            }
                        }
                        else
                        {
                            translations = dictionary.Translations.ToList();
                        }

                        foreach (var traslation in translations) 
                        {
                            if (!traslation.AttributeValues.Any())
                            {
                                for (int i = 0; i < attributeValueCount + (random.Next(2) == 0 ? random.Next(attributeValueCountRandomRange) : -random.Next(attributeValueCountRandomRange)); i++)
                                {
                                    var attribute = attributes.ElementAt(random.Next(attributes.Count));
                                    var idx = random.Next(loremIpsum.Length - 6);
                                    var attributeValue = new AttributeValue();

                                    if (attribute.Type == AttributeType.spinner && attribute.AttributeParameters.Any())
                                    {
                                        var attributeParameters = attribute.AttributeParameters;
                                        attributeValue = new AttributeValue
                                        {
                                            Value = attributeParameters.ElementAt(random.Next(attributeParameters.Count)).Value,
                                            Translation = traslation,
                                            Attribute = attribute
                                        };
                                    }
                                    else
                                    {
                                        attributeValue = new AttributeValue
                                        {
                                            Value = loremIpsum.CustomSubstring(idx, 6, stringRemoves),
                                            Translation = traslation,
                                            Attribute = attribute
                                        };
                                    }

                                    context.Add(attributeValue);
                                }
                            }
                        }

                        if (!context.DictionaryLanguages.Where(dl => dl.DictionaryId == dictionary.Id).Any())
                        {
                            int firstLanguage = random.Next(languages.Count);

                            context.Add(new DictionaryLanguage
                            {
                                Type = LanguageType.source,
                                Dictionary = dictionary,
                                Language = languages.ElementAt(firstLanguage)
                            });

                            int secondLanguage;
                            do
                            {
                                secondLanguage = random.Next(languages.Count);
                            }
                            while (firstLanguage == secondLanguage);

                            context.Add(new DictionaryLanguage
                            {
                                Type = LanguageType.destination,
                                Dictionary = dictionary,
                                Language = languages.ElementAt(secondLanguage)
                            });
                        }

                        if (!context.UserDictionaries.Where(ud => ud.Type == UserType.owner &&
                                                            ud.UserId == user.Id &&
                                                            ud.DictionaryId == dictionary.Id)
                                                     .Any())
                        {
                            context.Add(new UserDictionary
                            {
                                Type = UserType.owner,
                                Dictionary = dictionary,
                                User = user
                            });
                        }
                    }

                    await context.SaveChangesAsync();
                }

                foreach(var user in await context.Users.Include(u => u.UserDictionaries).ToListAsync())
                {
                    if (!user.UserDictionaries.Any(ud => ud.Type == UserType.viewer))
                    {
                        var publicDictionaries = await context.UserDictionaries
                                                              .Include(ud => ud.Dictionary)
                                                              .Where(ud => !ud.Dictionary
                                                                              .UserDictionaries
                                                                              .Any(ud => ud.UserId == user.Id &&
                                                                                         ud.Type == UserType.owner) &&
                                                                            ud.Dictionary.IsPublic)
                                                              .Select(ud => ud.Dictionary)
                                                              
                                                              .ToListAsync();

                        if (publicDictionaries.Count >= viewerCount)
                        {
                            for (int i = 0; i < viewerCount; i++)
                            {
                                var publicDictionary = publicDictionaries.ElementAt(random.Next(publicDictionaries.Count));
                                context.Add(new UserDictionary
                                {
                                    Type = UserType.viewer,
                                    Dictionary = publicDictionary,
                                    User = user
                                });
                                publicDictionaries.Remove(publicDictionary);
                            }
                        }
                    }
                }

                await context.SaveChangesAsync();
            }
        }
    }
}
