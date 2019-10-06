using MEMO.DTO.Enums;
using System;
using System.Collections.Generic;
using System.Text;

namespace MEMO.DAL.Entities
{
    public class DictionaryLanguage
    {
        public Guid Id { get; set; }
        public LanguageType Type { get; set; }

        public Guid DictionaryId { get; set; }
        public Dictionary Dictionary { get; set; }

        public Guid LanguageId { get; set; }
        public Language Language { get; set; }
    }
}
