using MEMO.DAL.Interfaces;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text;

namespace MEMO.DAL.Entities
{
    public class Translation : IAuditable
    {
        public Guid Id { get; set; }
        [MaxLength(30)]
        public string Original { get; set; }
        [MaxLength(30)]
        public string Translated { get; set; }
        public int Color { get; set; }

        public Guid DictionaryId { get; set; }
        public Dictionary Dictionary { get; set; }
        public DateTime CreationDate { get; set; }

        [NotMapped]
        public int AttributeValueCount { get; set; }

        public ICollection<AttributeValue> AttributeValues { get; set; } = new List<AttributeValue>();
    }
}
