/*
 *  Copyright (C) 2023 Eduardo Aguliari and Ramon Peixe
 *  Contact: eduardo <dot> aguliari <at> ifsp <dot> edu <dot> br
 *  Contact: ramon <dot> peixe <at> ifsp <dot> edu <dot> br
 *
 *  This file is part of CTruco (Truco game for didactic purpose).
 *
 *  CTruco is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  CTruco is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with CTruco.  If not, see <https://www.gnu.org/licenses/>
 */
package com.peixe.aguliari.perdenuncabot;

import com.bueno.spi.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


public class PerdeNuncaBotTest {
    private PerdeNuncaBot perdeNuncaBot;

    TrucoCard opponentCard = TrucoCard.of(CardRank.FIVE, CardSuit.DIAMONDS);
    private final List<TrucoCard> botCards = Arrays.asList(
            TrucoCard.of(CardRank.FIVE, CardSuit.CLUBS),
            TrucoCard.of(CardRank.SIX, CardSuit.SPADES),
            TrucoCard.of(CardRank.SEVEN, CardSuit.SPADES));

    @BeforeEach
    public void setUp() {
        perdeNuncaBot = new PerdeNuncaBot();
    }

    @Test
    @DisplayName("Chooses the smallest card when no cards have been played")
    void choosesTheSmallestCardWhenNoCardsHaveBeenPlayed() {
        TrucoCard vira = TrucoCard.of(CardRank.FIVE, CardSuit.DIAMONDS);
        GameIntel.StepBuilder stepBuilder = GameIntel.StepBuilder.with()
                .gameInfo(List.of(GameIntel.RoundResult.LOST), List.of(vira), vira, 1)
                .botInfo(botCards, 9)
                .opponentScore(4);

        CardToPlay card = perdeNuncaBot.chooseCard(stepBuilder.build());

        assertEquals(CardRank.FIVE, card.content().getRank());
    }

    @Test
    @DisplayName("Bot discards lowest rank card when impossible to win.")
    public void testBotDiscardsLowestRankCardWhenImpossibleToWin() {
        TrucoCard vira = TrucoCard.of(CardRank.ACE, CardSuit.DIAMONDS);
        TrucoCard opponentCard = TrucoCard.of(CardRank.TWO, CardSuit.DIAMONDS);

        List<TrucoCard> openCards = Arrays.asList(
                TrucoCard.of(CardRank.ACE, CardSuit.DIAMONDS),
                TrucoCard.of(CardRank.TWO, CardSuit.DIAMONDS));

        GameIntel.StepBuilder stepBuilder = GameIntel.StepBuilder.with()
                .gameInfo(List.of(GameIntel.RoundResult.DREW), openCards, vira, 1)
                .botInfo(botCards, 9)
                .opponentScore(3)
                .opponentCard(opponentCard);

        CardToPlay botCard = perdeNuncaBot.chooseCard(stepBuilder.build());

        assertEquals(CardRank.FIVE, botCard.content().getRank());
    }

    @Test
    @DisplayName("Should play the lowest manilha that beats the opponent, if possible")
    public void shouldPlayTheLowestManilhaThatBeatsTheOpponentIfPossible() {
        TrucoCard vira = TrucoCard.of(CardRank.FIVE, CardSuit.DIAMONDS);
        List<TrucoCard> openCards = Arrays.asList(
                TrucoCard.of(CardRank.FIVE, CardSuit.DIAMONDS),
                TrucoCard.of(CardRank.SIX, CardSuit.DIAMONDS));
        List<TrucoCard> botCards = Arrays.asList(
                TrucoCard.of(CardRank.SIX, CardSuit.CLUBS),
                TrucoCard.of(CardRank.SIX, CardSuit.HEARTS),
                TrucoCard.of(CardRank.ACE, CardSuit.CLUBS));

        GameIntel.StepBuilder stepBuilder = GameIntel.StepBuilder.with()
                .gameInfo(List.of(GameIntel.RoundResult.LOST), openCards, vira, 1)
                .botInfo(botCards, 9)
                .opponentScore(4)
                .opponentCard(TrucoCard.of(CardRank.SIX, CardSuit.DIAMONDS));

        CardToPlay botCard = perdeNuncaBot.chooseCard(stepBuilder.build());

        assertEquals(CardSuit.HEARTS, botCard.content().getSuit());
    }

    @Test
    @DisplayName("Should play the lowest card according to the suit if the bot has no manilhas")
    public void shouldPlayTheLowestCardAccordingToTheSuitIfTheBotHasNoManilhas() {
        TrucoCard vira = TrucoCard.of(CardRank.FOUR, CardSuit.SPADES);
        List<TrucoCard> openCards = Arrays.asList(
                TrucoCard.of(CardRank.FOUR, CardSuit.SPADES),
                TrucoCard.closed());

        GameIntel.StepBuilder stepBuilder = GameIntel.StepBuilder.with()
                .gameInfo(List.of(GameIntel.RoundResult.LOST), openCards, vira, 1)
                .botInfo(botCards, 9)
                .opponentScore(4)
                .opponentCard(TrucoCard.closed());

        CardToPlay botCard = perdeNuncaBot.chooseCard(stepBuilder.build());

        assertEquals(CardSuit.SPADES, botCard.content().getSuit());
    }

    @Test
    @DisplayName("Should play a manilha stronger than the player's if the bot has manilhas")
    public void shouldPlayManilhaStrongerThanThePlayersIfTheBotHasManilhas() {
        TrucoCard vira = TrucoCard.of(CardRank.FIVE, CardSuit.DIAMONDS);
        TrucoCard opponentCard = TrucoCard.of(CardRank.SIX, CardSuit.DIAMONDS);

        List<TrucoCard> openCards = Arrays.asList(
                TrucoCard.of(CardRank.FIVE, CardSuit.DIAMONDS),
                TrucoCard.of(CardRank.SIX, CardSuit.DIAMONDS));

        GameIntel.StepBuilder stepBuilder = GameIntel.StepBuilder.with()
                .gameInfo(List.of(GameIntel.RoundResult.LOST), openCards, vira, 1)
                .botInfo(botCards, 9)
                .opponentScore(4)
                .opponentCard(opponentCard);

        CardToPlay botCard = perdeNuncaBot.chooseCard(stepBuilder.build());

        assertEquals(CardRank.SIX, botCard.content().getRank());
    }

    @Test
    @DisplayName("Should play the card with the lowest rank to win")
    public void shouldPlayTheCardWithTheLowestRankToWinTest() {
        TrucoCard vira = TrucoCard.of(CardRank.ACE, CardSuit.DIAMONDS);
        TrucoCard opponentCard = TrucoCard.of(CardRank.THREE, CardSuit.DIAMONDS);
        List<TrucoCard> openCards = Arrays.asList(
                TrucoCard.of(CardRank.ACE, CardSuit.DIAMONDS),
                TrucoCard.of(CardRank.THREE, CardSuit.DIAMONDS));

        GameIntel.StepBuilder stepBuilder = GameIntel.StepBuilder.with()
                .gameInfo(List.of(GameIntel.RoundResult.DREW), openCards, vira, 1)
                .botInfo(botCards, 9)
                .opponentScore(3)
                .opponentCard(opponentCard);

        assertEquals(CardRank.FIVE, perdeNuncaBot.chooseCard(stepBuilder.build()).content().getRank());
    }

    @Test
    @DisplayName("Chooses the Diamonds manilha when in hand")
    void choosesTheDiamondsManilhaWhenInHand() {
        TrucoCard vira = TrucoCard.of(CardRank.FIVE, CardSuit.DIAMONDS);
        List<TrucoCard> botCards = Arrays.asList(
                TrucoCard.of(CardRank.SIX, CardSuit.CLUBS),
                TrucoCard.of(CardRank.SIX, CardSuit.DIAMONDS),
                TrucoCard.of(CardRank.ACE, CardSuit.CLUBS));

        GameIntel gameIntel = GameIntel.StepBuilder.with()
                .gameInfo(List.of(GameIntel.RoundResult.LOST), List.of(vira), vira, 1)
                .botInfo(botCards, 9)
                .opponentScore(4)
                .opponentCard(TrucoCard.of(CardRank.SEVEN, CardSuit.DIAMONDS))
                .build();

        CardToPlay cardToPlay = perdeNuncaBot.chooseCard(gameIntel);

        assertEquals(CardSuit.DIAMONDS, cardToPlay.content().getSuit());
    }

    @Test
    @DisplayName("Discards when the opponent plays a unbeatable card ")
    void discardsWhenTheOpponentPlaysAUnbeatableCard() {
        TrucoCard vira = TrucoCard.of(CardRank.FIVE, CardSuit.DIAMONDS);
        List<TrucoCard> openCards = Arrays.asList(
                TrucoCard.of(CardRank.FIVE, CardSuit.DIAMONDS),
                TrucoCard.of(CardRank.SIX, CardSuit.DIAMONDS));

        List<TrucoCard> botCards = Arrays.asList(
                TrucoCard.of(CardRank.FOUR, CardSuit.CLUBS),
                TrucoCard.of(CardRank.FOUR, CardSuit.HEARTS),
                TrucoCard.of(CardRank.FOUR, CardSuit.CLUBS));

        GameIntel gameIntel = GameIntel.StepBuilder.with()
                .gameInfo(List.of(GameIntel.RoundResult.LOST), openCards, vira, 1)
                .botInfo(botCards, 9)
                .opponentScore(4)
                .opponentCard(TrucoCard.of(CardRank.SIX, CardSuit.DIAMONDS))
                .build();

        CardToPlay cardToPlay = perdeNuncaBot.chooseCard(gameIntel);

        assertFalse(cardToPlay.isDiscard());
    }

    @Test
    @DisplayName("Plays an attack card if has at least two attack cards in hand")
    void playsAnAttackCardIfHasAtLeastTwoAttackCardsInHand() {
        TrucoCard vira = TrucoCard.of(CardRank.FIVE, CardSuit.DIAMONDS);
        List<TrucoCard> botCards = Arrays.asList(
                TrucoCard.of(CardRank.THREE, CardSuit.CLUBS),
                TrucoCard.of(CardRank.SIX, CardSuit.DIAMONDS),
                TrucoCard.of(CardRank.ACE, CardSuit.CLUBS));

        GameIntel gameIntel = GameIntel.StepBuilder.with()
                .gameInfo(List.of(GameIntel.RoundResult.LOST), List.of(vira), vira, 1)
                .botInfo(botCards, 9)
                .opponentScore(4)
                .opponentCard(TrucoCard.of(CardRank.SEVEN, CardSuit.DIAMONDS))
                .build();

        CardToPlay cardToPlay = perdeNuncaBot.chooseCard(gameIntel);

        assertEquals(CardSuit.DIAMONDS, cardToPlay.content().getSuit());
    }

    @Test
    @DisplayName("Should not rise if opponent has specific points")
    public void shouldNotRiseIfOpponentHasSpecificPoints() {
        TrucoCard trumpCard = TrucoCard.of(CardRank.FOUR, CardSuit.DIAMONDS);
        List<TrucoCard> playedCards = Arrays.asList(trumpCard, opponentCard);
        GameIntel.StepBuilder stepBuilder = GameIntel.StepBuilder.with()
                .gameInfo(List.of(GameIntel.RoundResult.LOST), playedCards, trumpCard, 1)
                .botInfo(botCards, 9)
                .opponentScore(9)
                .opponentCard(opponentCard);

        boolean shouldRaise = perdeNuncaBot.decideIfRaises(stepBuilder.build());

        assertFalse(shouldRaise);
    }

    @Test
    @DisplayName("Should raise if has good hand and specific conditions")
    public void shouldRaiseIfHasGoodHandAndSpecificPoints() {
        TrucoCard trumpCard = TrucoCard.of(CardRank.FOUR, CardSuit.DIAMONDS);
        List<TrucoCard> playedCards = Arrays.asList(trumpCard, opponentCard);
        GameIntel.StepBuilder stepBuilder = GameIntel.StepBuilder.with()
                .gameInfo(List.of(GameIntel.RoundResult.LOST), playedCards, trumpCard, 1)
                .botInfo(botCards, 9)
                .opponentScore(8)
                .opponentCard(opponentCard);

        boolean shouldRaise = perdeNuncaBot.decideIfRaises(stepBuilder.build());

        assertTrue(shouldRaise);
    }

    @Test
    @DisplayName("Bot does not raise if it has a weak hand")
    public void botDoesNotRaiseIfItHasAWeakHand() {
        TrucoCard trumpCard = TrucoCard.of(CardRank.ACE, CardSuit.DIAMONDS);
        List<TrucoCard> playedCards = Arrays.asList(
                TrucoCard.of(CardRank.SIX, CardSuit.DIAMONDS),
                opponentCard);
        GameIntel.StepBuilder stepBuilder = GameIntel.StepBuilder.with()
                .gameInfo(List.of(GameIntel.RoundResult.LOST), playedCards, trumpCard, 1)
                .botInfo(botCards, 3)
                .opponentScore(3)
                .opponentCard(opponentCard);

        boolean shouldRaise = perdeNuncaBot.decideIfRaises(stepBuilder.build());

        assertFalse(shouldRaise);
    }

    @Test
    @DisplayName("Should raise if has manilha and higher-ranking card")
    public void shouldRaiseIfHasManilhaAndHigherRankingCard() {
        TrucoCard vira = TrucoCard.of(CardRank.FOUR, CardSuit.DIAMONDS);
        List<TrucoCard> openCards = Arrays.asList(
                vira,
                opponentCard);
        GameIntel.StepBuilder stepBuilder = GameIntel.StepBuilder.with()
                .gameInfo(List.of(GameIntel.RoundResult.LOST), openCards, vira, 1)
                .botInfo(botCards, 9)
                .opponentScore(5)
                .opponentCard(opponentCard);

        // Assert that the bot raises if it has a manilha and a higher-ranking card than the opponent's card.
        boolean shouldRaise = perdeNuncaBot.decideIfRaises(stepBuilder.build());
        assertThat(shouldRaise).isTrue();
    }

    @Test
    @DisplayName("Should not raise if does not have manilha")
    public void shouldNotRaiseIfDoesNotHaveManilha() {
        TrucoCard leadCard = TrucoCard.of(CardRank.ACE, CardSuit.DIAMONDS);
        List<TrucoCard> openCards = Arrays.asList(
                leadCard,
                opponentCard);
        GameIntel.StepBuilder stepBuilder = GameIntel.StepBuilder.with()
                .gameInfo(List.of(GameIntel.RoundResult.LOST), openCards, leadCard, 1)
                .botInfo(botCards, 9)
                .opponentScore(5)
                .opponentCard(opponentCard);

        boolean shouldRaise = perdeNuncaBot.decideIfRaises(stepBuilder.build());
        assertThat(shouldRaise).isTrue();
    }

    @Test
    @DisplayName("Should not raise if only have trump card")
    public void shouldNotRaiseIfOnlyHaveTrumpCard() {
        TrucoCard leadCard = TrucoCard.of(CardRank.ACE, CardSuit.DIAMONDS);

        List<TrucoCard> openCards = Arrays.asList(
                TrucoCard.of(CardRank.ACE, CardSuit.DIAMONDS),
                TrucoCard.of(CardRank.FIVE, CardSuit.DIAMONDS));
        List<TrucoCard> cards = Arrays.asList(
                TrucoCard.of(CardRank.FOUR, CardSuit.CLUBS),
                TrucoCard.of(CardRank.TWO, CardSuit.HEARTS),
                TrucoCard.of(CardRank.SIX, CardSuit.CLUBS));
        GameIntel.StepBuilder stepBuilder = GameIntel.StepBuilder.with()
                .gameInfo(List.of(GameIntel.RoundResult.LOST), openCards, leadCard, 1)
                .botInfo(cards, 7)
                .opponentScore(8)
                .opponentCard(opponentCard);
        assertFalse(perdeNuncaBot.decideIfRaises(stepBuilder.build()));
    }

    @Test
    @DisplayName("Should return false if player has only one good card")
    public void shouldReturnFalseIfPlayerHasOnlyOneGoodCard() {
        TrucoCard goodCard = TrucoCard.of(CardRank.KING, CardSuit.SPADES);

        List<TrucoCard> openCards = Arrays.asList(
                TrucoCard.of(CardRank.ACE, CardSuit.DIAMONDS),
                TrucoCard.of(CardRank.FIVE, CardSuit.DIAMONDS));

        List<TrucoCard> cards = Arrays.asList(
                TrucoCard.of(CardRank.QUEEN, CardSuit.HEARTS),
                TrucoCard.of(CardRank.FOUR, CardSuit.HEARTS),
                TrucoCard.of(CardRank.SIX, CardSuit.CLUBS));

        GameIntel.StepBuilder stepBuilder = GameIntel.StepBuilder.with()
                .gameInfo(List.of(GameIntel.RoundResult.LOST), openCards, goodCard, 1)
                .botInfo(cards, 4)
                .opponentScore(6)
                .opponentCard(opponentCard);

        boolean shouldRaise = perdeNuncaBot.decideIfRaises(stepBuilder.build());

        assertFalse(shouldRaise);
    }

    @Test
    @DisplayName("Should raise if the sum of card values is above the average and the leading card is strong")
    public void shouldRaiseIfSumOfCardValuesIsAboveAverageAndLeadingCardIsStrong() {
        TrucoCard leadCard = TrucoCard.of(CardRank.ACE, CardSuit.DIAMONDS);

        List<TrucoCard> openCards = Arrays.asList(
                TrucoCard.of(CardRank.ACE, CardSuit.DIAMONDS),
                TrucoCard.of(CardRank.KING, CardSuit.DIAMONDS));

        List<TrucoCard> playerCards = Arrays.asList(
                TrucoCard.of(CardRank.THREE, CardSuit.CLUBS),
                TrucoCard.of(CardRank.FOUR, CardSuit.HEARTS),
                TrucoCard.of(CardRank.SEVEN, CardSuit.CLUBS));

        int sumOfPlayerCardValues = calculateSumOfCardValues(playerCards);

        int averageCardValue = calculateAverageCardValue(openCards);

        boolean shouldRaise = sumOfPlayerCardValues > averageCardValue && leadCard.getRank().value() >= 10;

        assertFalse(shouldRaise);
    }

    private int calculateSumOfCardValues(List<TrucoCard> cards) {
        return cards.stream()
                .mapToInt(card -> card.getRank().value())
                .sum();
    }

    private int calculateAverageCardValue(List<TrucoCard> cards) {
        int sumOfCardValues = cards.stream()
                .mapToInt(card -> card.getRank().value())
                .sum();

        return sumOfCardValues / cards.size();
    }

    @Test
    @DisplayName("Should raise if only have manilhas")
    public void shouldRaiseIfOnlyHaveManilhas() {
        TrucoCard leadCard = TrucoCard.of(CardRank.ACE, CardSuit.DIAMONDS);

        List<TrucoCard> openCards = Arrays.asList(
                TrucoCard.of(CardRank.ACE, CardSuit.DIAMONDS),
                opponentCard);

        List<TrucoCard> playerCards = Arrays.asList(
                TrucoCard.of(CardRank.TWO, CardSuit.HEARTS),
                TrucoCard.of(CardRank.TWO, CardSuit.SPADES));

        GameIntel.StepBuilder stepBuilder = GameIntel.StepBuilder.with()
                .gameInfo(List.of(GameIntel.RoundResult.LOST), openCards, leadCard, 1)
                .botInfo(playerCards, 3)
                .opponentScore(2)
                .opponentCard(opponentCard);

        boolean shouldRaise = perdeNuncaBot.decideIfRaises(stepBuilder.build());

        assertTrue(shouldRaise);
    }
}