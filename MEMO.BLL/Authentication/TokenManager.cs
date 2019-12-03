using MEMO.DAL.Entities;
using Microsoft.IdentityModel.Tokens;
using System;
using System.Collections.Generic;
using System.IdentityModel.Tokens.Jwt;
using System.Linq;
using System.Security.Claims;
using System.Text;

namespace MEMO.BLL.Authentication
{
    public class TokenManager
    {
        private readonly string _secret;
        private readonly JwtSecurityTokenHandler _tokenHandler;

        public TokenManager(String secret)
        {
            _secret = secret;
            _tokenHandler = new JwtSecurityTokenHandler();
        }

        public string GenerateToken(User user)
        {
            var signInKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(_secret));

            var tokenDescriptor = new SecurityTokenDescriptor
            {
                Subject = new ClaimsIdentity(new Claim[]
                {
                    new Claim(ClaimTypes.Name, user.Id.ToString()),
                    new Claim(ClaimTypes.Role, user.Role)
                }),
                Expires = DateTime.UtcNow.AddDays(1),
                SigningCredentials = new SigningCredentials(signInKey, SecurityAlgorithms.HmacSha256)
            };

            var token = _tokenHandler.CreateToken(tokenDescriptor);

            return _tokenHandler.WriteToken(token);
        }
        public string DecodeUserId(string token)
        {
            var decodedToken = _tokenHandler.ReadToken(token) as JwtSecurityToken;

            return decodedToken.Claims.FirstOrDefault(c => c.Type == "unique_name").Value;
        }

        public string DecodeUserRole(string token)
        {
            var decodedToken = _tokenHandler.ReadToken(token) as JwtSecurityToken;

            return decodedToken.Claims.FirstOrDefault(c => c.Type == "role").Value;
        }
    }
}
