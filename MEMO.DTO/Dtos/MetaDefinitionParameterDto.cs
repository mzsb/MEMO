using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Text;

namespace MEMO.DTO
{
    public class MetaDefinitionParameterDto : DtoBase
    {
        [Required(ErrorMessage = "MetaDefinitionParameter value is required", AllowEmptyStrings = false)]
        public string Value { get; set; }
    }
}
