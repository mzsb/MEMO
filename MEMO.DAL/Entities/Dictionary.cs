using MEMO.DAL.Interfaces;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text;

namespace MEMO.DAL.Entities
{
    public class Dictionary : IAuditable
    {
        public Guid Id { get; set; }
        [MaxLength(20)]
        public string Name { get; set; }
        [MaxLength(250)]
        public string Description { get; set; }
        public bool IsPublic { get; set; }
        public bool IsFastAccessible { get; set; }

        [NotMapped]
        public int TranslationCount { get; set; }

        [NotMapped]
        public int ViewerCount { get; set; }

        public ICollection<Translation> Translations { get; set; } = new List<Translation>();

        [MinLength(1)]
        public ICollection<UserDictionary> UserDictionaries { get; set; } = new List<UserDictionary>();

        [MaxLength(2)]
        public ICollection<DictionaryLanguage> DictionaryLanguages { get; set; } = new List<DictionaryLanguage>();
        
        public DateTime CreationDate { get; set; }
    }
}
