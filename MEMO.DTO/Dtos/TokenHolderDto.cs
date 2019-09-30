using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Text;

namespace MEMO.DTO
{
    public class TokenHolderDto
    {
        public Guid UserId { get; set; }

        [Required(ErrorMessage = "Token is required to login automatically", AllowEmptyStrings = false)]
        public string Token { get; set; }
    }
}
