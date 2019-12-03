using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Text;

namespace MEMO.DAL.Entities
{
    public class AttributeParameter
    {
        public Guid Id { get; set; }

        [MaxLength(15)]
        public string Value { get; set; }

        public Guid AttributeId { get; set; }
        public Attribute Attribute { get; set; }
    }
}
