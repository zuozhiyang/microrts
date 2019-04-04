package ai;

import ai.core.AI;
import ai.core.ParameterSpecification;
import rts.*;
import rts.units.Unit;
import util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MixtureAI extends AI {
    public AI bot_a;
    public AI bot_b;
    public boolean unit_level = false;
    public double prob_a = .5;
    static Random random = new Random();

    public MixtureAI(AI a, AI b, double p, boolean u_level) {
        bot_a = a;
        bot_b = b;
        prob_a = p;
        unit_level = u_level;
    }

    public MixtureAI(AI a, AI b) {
        bot_a = a;
        bot_b = b;
    }

    @Override
    public void reset() {

    }

    @Override
    public PlayerAction getAction(int player, GameState gs) throws Exception {
        PlayerAction result = new PlayerAction();
        if (unit_level) {
            HashMap<Unit, UnitAction[]> map = new HashMap<>();
            PlayerAction playerAction1 = bot_a.getAction(player, gs);
            for (Pair<Unit, UnitAction> action : playerAction1.getActions()) {
                if (!map.containsKey(action.m_a)) {
                    map.put(action.m_a, new UnitAction[2]);
                }
                map.get(action.m_a)[0] = (action.m_b);
            }
            PlayerAction playerAction2 = bot_b.getAction(player, gs);
            for (Pair<Unit, UnitAction> action : playerAction2.getActions()) {
                if (!map.containsKey(action.m_a)) {
                    map.put(action.m_a, new UnitAction[2]);
                }
                map.get(action.m_a)[1] = (action.m_b);
            }
            for (Unit unit : map.keySet()) {
                int index = random.nextDouble() < prob_a ? 0 : 1;
                UnitAction ua = map.get(unit)[index];
                if (ua.resourceUsage(unit, gs.getPhysicalGameState()).consistentWith(result.getResourceUsage(), gs)) {
                    ResourceUsage ru = ua.resourceUsage(unit, gs.getPhysicalGameState());
                    result.getResourceUsage().merge(ru);
                    result.addUnitAction(unit, ua);
                } else {
                    result.addUnitAction(unit, null);
                }
            }
            return result;
        } else {
            AI ai = random.nextDouble() < prob_a ? bot_a : bot_b;
            return ai.getAction(player, gs);
        }
    }

    @Override
    public AI clone() {
        return new MixtureAI(bot_a, bot_b, prob_a, unit_level);
    }

    @Override
    public List<ParameterSpecification> getParameters() {
        return null;
    }
}
