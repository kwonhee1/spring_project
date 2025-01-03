package com.example.demo.repository;

import com.example.demo.model.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface MemberRepository {
    // register
    public void addMember(@Param("member") Member member);
    // check Email
    public boolean isExistEmail(String email);

}
