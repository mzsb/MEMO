using AutoMapper;
using MEMO.BLL.Interfaces;
using MEMO.DAL.Entities;
using MEMO.DTO;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace MEMO.WebAPI.Controllers
{
    [Route("api/[Controller]")]
    public class UserController : ControllerBase
    {
        private readonly IUserService _service;
        private readonly IMapper _mapper;

        public UserController(IUserService userService,
                              IMapper mapper)
        {
            _service = userService;
            _mapper = mapper;
        }

        [Authorize(Roles = "Administrator")]
        [HttpGet]
        public async Task<ActionResult<IEnumerable<UserDto>>> GetAsync()
        {
            return _mapper.Map<List<UserDto>>(await _service.GetAsync());
        }

        [Authorize(Roles = "Administrator,User")]
        [HttpGet("{id}")]
        public async Task<ActionResult<UserDto>> GetByIdAsync(Guid id)
        {
            return _mapper.Map<UserDto>(await _service.GetByIdAsync(id));
        }

        [Authorize(Roles = "Administrator,User")]
        [HttpPut]
        public async Task<ActionResult> UpdateAsync([FromBody] UserDto user)
        {
            _service.Token = await getAccessToken();

            await _service.UpdateAsync(_mapper.Map<User>(user));

            return Ok();
        }

        [Authorize(Roles = "Administrator,User")]
        [HttpDelete("{id}")]
        public async Task<ActionResult> DeleteAsync(Guid id)
        {
            _service.Token = await getAccessToken();

            await _service.DeleteAsync(id);

            return Ok();
        }

        [Authorize(Roles = "Administrator,User")]
        [HttpGet("viewers/{id}")]
        public async Task<IEnumerable<UserDto>> GetViewersByUserIdAsync(Guid id)
        {
            return _mapper.Map<List<UserDto>>(await _service.GetViewersByUserIdAsync(id));
        }


        private async Task<string> getAccessToken() => await HttpContext.GetTokenAsync("access_token");
    }
}
