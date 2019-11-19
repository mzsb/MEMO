using MEMO.DTO.Interfaces;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Text;

namespace MEMO.DTO
{
    public class TranslationDto : IAuditable
    {
        public Guid Id { get; set; }

        [Required(ErrorMessage = "Translation origin is required", AllowEmptyStrings = false)]
        public string Original { get; set; }
        public string Translated { get; set; }

        [Required(ErrorMessage = "Translation dictionaryId is required")]
        public Guid DictionaryId { get; set; }

        public ICollection<AttributeValueDto> TranslationMetas { get; set; } = new List<AttributeValueDto>();
        public String CreationDate { get; set; }

        public ICollection<AttributeValueDto> AttributeValues { get; set; } = new List<AttributeValueDto>();
    }
}
