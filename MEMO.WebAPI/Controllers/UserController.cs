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
    [Route("api/[Controller]")]
    public class UserController : Controller
    {
        private readonly IUserService _service;
        private readonly IMapper _mapper;

        public UserController(IUserService userService,
                              IMapper mapper)
        {
            _service = userService;
            _mapper = mapper;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<UserDto>>> GetAsync()
        {
            return _mapper.Map<List<UserDto>>(await _service.GetAsync());
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<UserDto>> GetByIdAsync(Guid id)
        {
            return _mapper.Map<UserDto>(await _service.GetByIdAsync(id));
        }

        [HttpPut]
        public async Task<ActionResult> UpdateAsync([FromBody] UserDto user)
        {
            await _service.UpdateAsync(_mapper.Map<User>(user));

            return Ok();
        }

        [HttpDelete("{id}")]
        public async Task<ActionResult> DeleteAsync(Guid id)
        {
            await _service.DeleteAsync(id);

            return Ok();
        }
    }
}
