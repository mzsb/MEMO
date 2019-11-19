using AutoMapper;
using MEMO.BLL.Authentication;
using MEMO.DTO;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using System.Threading.Tasks;

namespace MEMO.WebAPI.Controllers
{
    [AllowAnonymous]
    [Route("api/[Controller]")]
    public class AuthenticationController : ControllerBase
    {
        private readonly BLL.Interfaces.IAuthenticationService _authenticationService;
        private readonly IMapper _mapper;

        public AuthenticationController(BLL.Interfaces.IAuthenticationService authenticationService,
                                        IMapper mapper)
        {
            _authenticationService = authenticationService;
            _mapper = mapper;
        }

        [HttpPost("login")]
        public async Task<ActionResult<TokenHolderDto>> LoginAsync([FromBody] LoginDto login)
        {
            var tokenHolder = await _authenticationService.LoginAsync(_mapper.Map<Login>(login));

            return _mapper.Map<TokenHolderDto>(tokenHolder);
            
        }

        [HttpPost("autoLogin")]
        public async Task<ActionResult<TokenHolderDto>> AutoLoginAsync([FromBody] string token)
        {
            return _mapper.Map<TokenHolderDto>(await _authenticationService.AutoLoginAsync(token));
        }

        [HttpPost("registration")]
        public async Task<ActionResult<TokenHolderDto>> RegistrationAsync([FromBody] RegistrationDto registration)
        {
            var tokenHolder = await _authenticationService.RegistrationAsync(_mapper.Map<Registration>(registration));

            return _mapper.Map<TokenHolderDto>(tokenHolder);
        }

        [HttpPost("refreshToken")]
        public async Task<ActionResult<TokenHolderDto>> RefrehTokenAsync([FromBody] string token)
        {
            return _mapper.Map<TokenHolderDto>(await _authenticationService.RefreshTokenAsync(token));
        }
    }
}
