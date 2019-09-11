using MEMO.DAL.Entities;
using Microsoft.IdentityModel.Tokens;
using System;
using System.Collections.Generic;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;

namespace MEMO.BLL.Authentication
{
    public static class TokenManager
    {
        public static string Secret { get; } = "pJGsYKDSZ6P5ez7pjBpPAuuRQnZQxa";

        public static string GenerateToken(User user)
        {
            var signInKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(Secret));

            var claims = GetClaims(user);

            var token = new JwtSecurityToken(
                    notBefore: DateTime.UtcNow,
                    expires: DateTime.UtcNow.AddSeconds(1),
                    claims: claims,
                    signingCredentials: new SigningCredentials(signInKey, SecurityAlgorithms.HmacSha256)
            );

            return new JwtSecurityTokenHandler().WriteToken(token);
        }

        private static List<Claim> GetClaims(User user)
        {
            return new List<Claim>
            {
                    new Claim(JwtRegisteredClaimNames.Sub, user.UserName),
                    new Claim(JwtRegisteredClaimNames.Jti, Guid.NewGuid().ToString()),
                    new Claim(ClaimTypes.Role, user.Role)
            };
        }
    }
}
