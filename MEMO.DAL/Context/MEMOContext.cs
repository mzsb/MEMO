using MEMO.DAL.Entities;
using MEMO.DAL.Interfaces;
using MEMO.DAL.Enums;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Identity.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Storage.ValueConversion;
using System;
using System.Threading;
using System.Threading.Tasks;
using System.Linq;

namespace MEMO.DAL.Context
{
    public class MEMOContext : IdentityDbContext<User, IdentityRole<Guid>, Guid>
    {
        public DbSet<Dictionary> Dictionaries { get; set; }
        public DbSet<Language> Languages { get; set; }
        public DbSet<Translation> Translations { get; set; }
        public DbSet<AttributeValue> AttributeValues { get; set; }
        public DbSet<Entities.Attribute> Attributes { get; set; }
        public DbSet<AttributeParameter> AttributeParameters { get; set; }
        public DbSet<DictionaryLanguage> DictionaryLanguages { get; set; }
        public DbSet<UserDictionary> UserDictionaries { get; set; }

        public MEMOContext(DbContextOptions<MEMOContext> options) : base(options) { }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);

            const string AdministratorRoleName = "Administrator";

            modelBuilder.Entity<IdentityRole<Guid>>().HasData(new IdentityRole<Guid> 
            { 
                Id = Guid.NewGuid(),
                Name = AdministratorRoleName, 
                NormalizedName = AdministratorRoleName.ToUpper() 
            });

            const string UserRoleName = "User";

            modelBuilder.Entity<IdentityRole<Guid>>().HasData(new IdentityRole<Guid>
            {
                Id = Guid.NewGuid(),
                Name = UserRoleName,
                NormalizedName = UserRoleName.ToUpper()
            });

            var languageCodes = Enum.GetValues(typeof(LanguageCode)).Cast<LanguageCode>();

            foreach (var languageCode in languageCodes) 
            {
                modelBuilder.Entity<Language>().HasData(new Language
                {
                    Id = Guid.NewGuid(),
                    LanguageCode = languageCode
                });
            }

            modelBuilder.Entity<Language>()
                        .HasMany(l => l.DictionaryLanguages)
                        .WithOne(dl => dl.Language)
                        .OnDelete(DeleteBehavior.Restrict);

            modelBuilder.Entity<Entities.Attribute>()
                        .HasMany(a => a.AttributeValues)
                        .WithOne(av => av.Attribute)
                        .OnDelete(DeleteBehavior.Cascade);           

            var userTypeConverter = new EnumToStringConverter<UserType>();

            modelBuilder.Entity<UserDictionary>()
                        .Property(ud => ud.Type)
                        .HasConversion(userTypeConverter);


            var attributeTypeConverter = new EnumToStringConverter<AttributeType>();

            modelBuilder.Entity<Entities.Attribute>()
                        .Property(a => a.Type)
                        .HasConversion(attributeTypeConverter);

            var languageTypeConverter = new EnumToStringConverter<LanguageType>();

            modelBuilder.Entity<DictionaryLanguage>()
                        .Property(dl => dl.Type)
                        .HasConversion(languageTypeConverter);

            var languageCodeConverter = new EnumToStringConverter<LanguageCode>();

            modelBuilder.Entity<Language>()
                        .Property(l => l.LanguageCode)
                        .HasConversion(languageCodeConverter);
        }

        private void SetCreationDate()
        {
            foreach (var e in ChangeTracker.Entries<IAuditable>())
            {
                if (e.State.Equals(EntityState.Added))
                {
                    e.Entity.CreationDate = DateTime.Now;
                }
            }
        }

        public override int SaveChanges()
        {
            SetCreationDate();
            return base.SaveChanges();
        }

        public override int SaveChanges(bool acceptAllChangesOnSuccess)
        {
            SetCreationDate();
            return base.SaveChanges(acceptAllChangesOnSuccess);
        }

        public override Task<int> SaveChangesAsync(CancellationToken cancellationToken = default)
        {
            SetCreationDate();
            return base.SaveChangesAsync(cancellationToken);
        }

        public override Task<int> SaveChangesAsync(bool acceptAllChangesOnSuccess, CancellationToken cancellationToken = default)
        {
            SetCreationDate();
            return base.SaveChangesAsync(acceptAllChangesOnSuccess, cancellationToken);
        }
    }
}
