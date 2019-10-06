using System;
using System.Collections.Generic;
using System.Text;

namespace MEMO.DAL.Entities
{
    public class MetaDefinitionParameter : EntityBase
    {
        public string Value { get; set; }

        public Guid MetaDefinitionId { get; set; }
        public MetaDefinition MetaDefinition { get; set; }
    }
}
