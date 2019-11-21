using MEMO.DAL.Enums;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text;

namespace MEMO.DAL.Entities
{
    public class Attribute
    {
        public Guid Id { get; set; }
        [MaxLength(15)]
        public string Name { get; set; }
        public AttributeType Type { get; set; }

        [NotMapped]
        public int AttributeValuesCount {get; set;}

        public Guid UserId { get; set; }
        public User User { get; set; }

        public ICollection<AttributeValue> AttributeValues { get; set; } = new List<AttributeValue>();
        public ICollection<AttributeParameter> AttributeParameters { get; set; } = new List<AttributeParameter>();
    }
}
