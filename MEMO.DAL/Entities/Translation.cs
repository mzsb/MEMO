using MEMO.DAL.Interfaces;
using System;
using System.Collections.Generic;
using System.Text;

namespace MEMO.DAL.Entities
{
    public class Translation : IAuditable
    {
        public Guid Id { get; set; } 
        public string Original { get; set; }
        public string Translated { get; set; }
        public Guid DictionaryId { get; set; }
        public Dictionary Dictionary { get; set; }
        public DateTime CreationDate { get; set; }

        public ICollection<AttributeValue> AttributeValues { get; set; } = new List<AttributeValue>();
    }
}
