package com.example.demo.repository;

import com.example.demo.model.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface MemberRepository {
    // register
    public abstract void addMember(@Param("member") Member member);
    // check Email
    public abstract boolean isExistEmail(String email);

}
