using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Text;

namespace MEMO.DTO
{
    public class TranslationMetaDto : DtoBase
    {
        [Required(ErrorMessage = "TranslationMeta value is required", AllowEmptyStrings = false)]
        public string Value { get; set; }

        [Required(ErrorMessage = "TranslationMeta metaDefinitionId is required")]
        public Guid MetaDefinitionId { get; set; }

        public MetaDefinitionDto MetaDefinition { get; set; }
    }
}
