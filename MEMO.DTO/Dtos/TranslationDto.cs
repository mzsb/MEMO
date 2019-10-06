using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Text;

namespace MEMO.DTO
{
    public class TranslationDto : DtoBase
    {
        [Required(ErrorMessage = "Translation value is required", AllowEmptyStrings = false)]
        public String Value { get; set; }

        [Required(ErrorMessage = "Translation dictionaryId is required")]
        public Guid DictionaryId { get; set; }

        public ICollection<TranslationMetaDto> TranslationMetas { get; set; } = new List<TranslationMetaDto>();
    }
}
