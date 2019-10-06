using System;
using System.Collections.Generic;
using System.Text;

namespace MEMO.DAL.Entities
{
    public class Language : EntityBase
    {
        public string Code { get; set; }
        public string Name { get; set; }

        public ICollection<DictionaryLanguage> DictionaryLanguages { get; set; } = new List<DictionaryLanguage>();
    }
}
