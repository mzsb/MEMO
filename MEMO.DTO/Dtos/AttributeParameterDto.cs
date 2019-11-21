using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Text;

namespace MEMO.DTO
{
    public class AttributeParameterDto
    {
        public Guid Id { get; set; }

        [Required(ErrorMessage = "AttributeParameter value is required", AllowEmptyStrings = false), MaxLength(10)]
        public string Value { get; set; }
    }
}
