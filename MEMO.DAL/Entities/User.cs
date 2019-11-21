using MEMO.DAL.Interfaces;
using Microsoft.AspNetCore.Identity;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace MEMO.DAL.Entities
{
    public class User : IdentityUser<Guid>, IAuditable
    {
        [NotMapped]
        public string Role { get; set; }

        [NotMapped]
        public int DictionaryCount { get; set; }
        [NotMapped]
        public int ViewedDictionaryCount { get; set; }
        [NotMapped]
        public int TranslationCount { get; set; }

        public ICollection<Attribute> Attributes { get; set; } = new List<Attribute>();
        public ICollection<UserDictionary> UserDictionaries { get; set; } = new List<UserDictionary>();
        public DateTime CreationDate { get; set; }
    }
}
