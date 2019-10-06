using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Text;

namespace MEMO.DAL.Entities
{
    public class Dictionary : EntityBase
    {
        public string Name { get; set; }
        public bool IsPublic { get; set; }

        public ICollection<Translation> Translations { get; set; } = new List<Translation>();
        public ICollection<MetaDefinition> MetaDefinitions { get; set; } = new List<MetaDefinition>();

        [MinLength(1)]
        public ICollection<UserDictionary> UserDictionaries { get; set; } = new List<UserDictionary>();

        [MaxLength(2)]
        public ICollection<DictionaryLanguage> DictionaryLanguages { get; set; } = new List<DictionaryLanguage>();
    }
}
