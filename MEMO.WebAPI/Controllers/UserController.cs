using AutoMapper;
using MEMO.BLL.Interfaces;
using MEMO.DAL.Entities;
using MEMO.WebAPI.Dtos;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace MEMO.WebAPI.Controllers
{
    [Route("api/User")]
    public class UserController : Controller
    {
        private readonly IUserService _userService;
        private readonly IMapper _mapper;

        public UserController(IUserService userService,
                              IMapper mapper)
        {
            _userService = userService;
            _mapper = mapper;
        }

        [AllowAnonymous]
        [HttpPost("login")]
        public async Task<ActionResult<UserDto>> LoginAsync([FromBody] UserDto user)
        {
            var mapped = _mapper.Map<User>(user);

            return _mapper.Map<UserDto>(await _userService.LoginAsync(mapped));
        }

        [AllowAnonymous]
        [HttpPost]
        public async Task<ActionResult<UserDto>> AddAsync([FromBody] UserDto user)
        {
            var mapped = _mapper.Map<User>(user);

            await _userService.AddAsync(mapped);

            return CreatedAtAction(nameof(GetByIdAsync), new { id = mapped.Id }, mapped);
        }

        [Authorize(Roles = "Administator,User")]
        [HttpGet]
        public async Task<ActionResult<IEnumerable<UserDto>>> GetAsync()
        {
            var users = await _userService.GetAsync();

            List<UserDto> mapped = _mapper.Map<List<UserDto>>(users);

            return mapped;
        }

        [Authorize(Roles = "Administator")]
        [HttpGet("{id}")]
        public async Task<ActionResult<UserDto>> GetByIdAsync(Guid id)
        {
            var user = await _userService.GetByIdAsync(id);

            UserDto mapped = _mapper.Map<UserDto>(user);

            return mapped;
        }

        [Authorize(Roles = "Administator,User")]
        [HttpPut]
        public async Task<ActionResult> Update([FromBody] UserDto user)
        {
            var mapped = _mapper.Map<User>(user);

            await _userService.UpdateAsync(mapped);

            return NoContent();
        }

        [Authorize(Roles = "Administator,User")]
        [HttpDelete]
        public async Task<ActionResult> Delete([FromBody] UserDto user)
        {
            var mapped = _mapper.Map<User>(user);

            await _userService.DeleteAsync(mapped);

            return NoContent();
        }
    }
}
