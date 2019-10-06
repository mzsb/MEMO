using MEMO.DAL.Entities;
using MEMO.DTO.Enums;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Identity.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Storage.ValueConversion;
using System;

namespace MEMO.DAL.Context
{
    public class MEMOContext : IdentityDbContext<User, IdentityRole<Guid>, Guid>
    {
        public DbSet<Dictionary> Dictionaries { get; set; }
        public DbSet<Language> Languages { get; set; }
        public DbSet<Translation> Translations { get; set; }
        public DbSet<TranslationMeta> TranslationMetas { get; set; }
        public DbSet<MetaDefinition> MetaDefinitions { get; set; }
        public DbSet<MetaDefinitionParameter> MetaDefinitionParameters { get; set; }

        public DbSet<DictionaryLanguage> DictionaryLanguages { get; set; }
        public DbSet<UserDictionary> UserDictionaries { get; set; }

        public MEMOContext(DbContextOptions<MEMOContext> options) : base(options) { }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);

            modelBuilder.Entity<Language>()
                        .HasMany(l => l.DictionaryLanguages)
                        .WithOne(dl => dl.Language)
                        .HasForeignKey(dl => dl.LanguageId)
                        .OnDelete(DeleteBehavior.Restrict);

            modelBuilder.Entity<Translation>()
                        .HasMany(t => t.TranslationMetas)
                        .WithOne(tm => tm.Translation)
                        .HasForeignKey(dl => dl.TranslationId)
                        .OnDelete(DeleteBehavior.Cascade);


            modelBuilder.Entity<TranslationMeta>()
                        .HasOne(tm => tm.MetaDefinition)
                        .WithMany(md => md.TranslationMetas)
                        .HasForeignKey(tm => tm.MetaDefinitionId)
                        .OnDelete(DeleteBehavior.Restrict);

            var userTypeConverter = new EnumToStringConverter<UserType>();

            modelBuilder.Entity<UserDictionary>()
                        .Property(ud => ud.Type)
                        .HasConversion(userTypeConverter);

            var metaTypeConverter = new EnumToStringConverter<MetaType>();

            modelBuilder.Entity<MetaDefinition>()
                        .Property(md => md.Type)
                        .HasConversion(metaTypeConverter);

            var languageTypeConverter = new EnumToStringConverter<LanguageType>();

            modelBuilder.Entity<DictionaryLanguage>()
                        .Property(dl => dl.Type)
                        .HasConversion(languageTypeConverter);
        }
    }
}
