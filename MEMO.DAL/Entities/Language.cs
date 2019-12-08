using MEMO.DAL.Enums;
using System;
using System.Collections.Generic;
using System.Text;

namespace MEMO.DAL.Entities
{
    public class Language
    {
        public Guid Id { get; set; }

        public LanguageCode LanguageCode { get; set; }

        public ICollection<DictionaryLanguage> DictionaryLanguages { get; set; } = new List<DictionaryLanguage>();
    }
}
