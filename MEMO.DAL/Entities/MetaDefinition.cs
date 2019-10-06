using MEMO.DTO.Enums;
using System;
using System.Collections.Generic;
using System.Text;

namespace MEMO.DAL.Entities
{
    public class MetaDefinition : EntityBase
    {
        public string Name { get; set; }
        public MetaType Type { get; set; }

        public Guid DictionaryId { get; set; }
        public Dictionary Dictionary { get; set; }

        public ICollection<TranslationMeta> TranslationMetas { get; set; } = new List<TranslationMeta>();
        public ICollection<MetaDefinitionParameter> MetaDefinitionParameters { get; set; } = new List<MetaDefinitionParameter>();
    }
}
