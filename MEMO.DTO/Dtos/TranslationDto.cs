using MEMO.DTO.Interfaces;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;

namespace MEMO.DTO
{
    public class TranslationDto : IAuditable
    {
        public Guid Id { get; set; }

        [Required(ErrorMessage = "Translation origin is required", AllowEmptyStrings = false), MaxLength(30)]
        public string Original { get; set; }

        [Required(ErrorMessage = "Translation translated is required", AllowEmptyStrings = false), MaxLength(30)]
        public string Translated { get; set; }

        [Required(ErrorMessage = "Translation dictionaryId is required")]
        public Guid DictionaryId { get; set; }

        public int Color { get; set; }

        public ICollection<AttributeValueDto> TranslationMetas { get; set; } = new List<AttributeValueDto>();

        public string CreationDate { get; set; }

        public int AttributeValueCount { get; set; }

        public ICollection<AttributeValueDto> AttributeValues { get; set; } = new List<AttributeValueDto>();
    }
}
