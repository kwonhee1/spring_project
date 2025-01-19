package com.example.demo.repository;

import com.example.demo.model.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
// spring boot mybatis를 이용 : repository는 interface로 선언해두고 user_repository.xml에 sql문법을 적어두면 알맞게 repository함수들을 구현해줌 (connection fool을 이용해서 짜줌)
public interface MemberRepository {
    // login security에서 login 진행방식과 알맞지 않음
    // public boolean login(String email, String password);

    // get Member from db for security : security에서 login할 때 email passwd확인을 위한 db 조회
    public Member getMemberByMemberId(int memberId);
    public Member getMemberByMemberEmail(String email);

    // register
    public int addMember(@Param("member") Member member);
    public void addRefresh(int userId);

    // check Email
    public boolean isExistEmail(String email);

}
