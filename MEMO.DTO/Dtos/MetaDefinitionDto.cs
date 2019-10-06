using MEMO.DTO.Enums;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Text;

namespace MEMO.DTO
{
    public class MetaDefinitionDto : DtoBase
    {
        [Required(ErrorMessage = "MetaDefinition name is required", AllowEmptyStrings = false)]
        public string Name { get; set; }
        public MetaType Type { get; set; }

        public ICollection<MetaDefinitionParameterDto> MetaDefinitionParameters { get; set; } = new List<MetaDefinitionParameterDto>();
    }
}
