using System;
using System.Collections.Generic;
using System.Text;

namespace MEMO.DAL.Entities
{
    public class TranslationMeta : EntityBase
    {
        public string Value { get; set; }

        public Guid TranslationId { get; set; }
        public Translation Translation { get; set; }

        public Guid MetaDefinitionId { get; set; }
        public MetaDefinition MetaDefinition { get; set; }
    }
}
