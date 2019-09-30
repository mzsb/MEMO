using AutoMapper;
using MEMO.BLL.Interfaces;
using MEMO.DAL.Entities;
using MEMO.DTO;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace MEMO.WebAPI.Controllers
{
    [Authorize(Roles = "Administator,User")]
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

        [HttpGet]
        public async Task<ActionResult<IEnumerable<UserDto>>> GetAsync()
        {
            var users = await _userService.GetAsync();

            List<UserDto> mapped = _mapper.Map<List<UserDto>>(users);

            return mapped;
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<UserDto>> GetByIdAsync(Guid id)
        {
            var user = await _userService.GetByIdAsync(id);

            UserDto mapped = _mapper.Map<UserDto>(user);

            return mapped;
        }

        [HttpPut]
        public async Task<ActionResult> Update([FromBody] UserDto user)
        {
            var mapped = _mapper.Map<User>(user);

            await _userService.UpdateAsync(mapped);

            return NoContent();
        }

        [HttpDelete]
        public async Task<ActionResult> Delete([FromBody] UserDto user)
        {
            var mapped = _mapper.Map<User>(user);

            await _userService.DeleteAsync(mapped);

            return NoContent();
        }
    }
}
