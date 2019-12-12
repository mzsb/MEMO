using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Text;

namespace MEMO.DAL.Entities
{
    public class AttributeValue
    {
        public Guid Id { get; set; }

        [MaxLength(70)]
        public string Value { get; set; }

        public Guid TranslationId { get; set; }

        public Translation Translation { get; set; }

        public Guid AttributeId { get; set; }

        public Attribute Attribute { get; set; }
    }
}
