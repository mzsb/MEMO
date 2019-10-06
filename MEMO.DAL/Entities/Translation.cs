using System;
using System.Collections.Generic;
using System.Text;

namespace MEMO.DAL.Entities
{
    public class Translation : EntityBase
    {
        public String Value { get; set; }
        public Guid DictionaryId { get; set; }
        public Dictionary Dictionary { get; set; }

        public ICollection<TranslationMeta> TranslationMetas { get; set; } = new List<TranslationMeta>();
    }
}
